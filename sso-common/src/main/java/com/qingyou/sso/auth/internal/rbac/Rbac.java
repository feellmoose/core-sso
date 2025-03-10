package com.qingyou.sso.auth.internal.rbac;

import com.qingyou.sso.auth.api.dto.Action;
import com.qingyou.sso.auth.exception.AuthException;
import com.qingyou.sso.domain.auth.Role;
import com.qingyou.sso.domain.auth.TargetRole;
import com.qingyou.sso.domain.auth.UserRole;
import com.qingyou.sso.utils.StringUtils;
import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class Rbac implements IRbac<RbacUserInfo, TargetInfo> {
    private final SqlClient client;

    @Override
    public Future<Boolean> enforce(Action<RbacUserInfo, TargetInfo> info) {
        return convertInfo(info).map(v -> {
            try {
                enforce0(v, info);
                return true;
            } catch (AuthException e) {
                return false;
            }
        });
    }

    @Override
    public Future<Void> enforceAndThrows(Action<RbacUserInfo, TargetInfo> info) {
        return convertInfo(info).<Void>map(action -> {
            enforce0(action, info);
            return null;
        }).onFailure(ex -> log.error("error", ex));
    }

    private Future<Action<List<UserRole>, TargetRole>> convertInfo(Action<RbacUserInfo, TargetInfo> info) {
        var targetInfo = info.target();
        var roles = client.preparedQuery("SELECT id,app_id,user_id,role_id FROM auth_rbac.user_role WHERE app_id = $1 AND user_id = $2")
                .execute(Tuple.of(targetInfo.appId(),info.owned().id()))
                .flatMap(rows -> {
                    Map<UserRole,Long> map = new HashMap<>();
                    for (Row row: rows){
                        UserRole role = new UserRole();
                        role.setId(row.getLong("id"));
                        role.setAppid(row.getLong("app_id"));
                        role.setUserId(row.getLong("user_id"));
                        map.put(role, row.getLong("role_id"));
                    }
                    return getRoles(List.copyOf(map.values())).map(rs -> {
                        Map<Long,Role> roleMap = rs.stream().collect(Collectors.toMap(Role::getId, r -> r));
                        return map.entrySet()
                                .stream()
                                .map(entry -> {
                                    entry.getKey().setRole(roleMap.get(entry.getValue()));
                                    return entry.getKey();
                                }).toList();
                    });
                });
        var target = client.preparedQuery("SELECT id,app_id,\"action\",object FROM auth_rbac.target WHERE app_id = $1 AND \"action\" = $2 AND object = $3")
                .execute(Tuple.of(targetInfo.appId(),targetInfo.action(),targetInfo.object()))
                .flatMap(rows -> {
                    for (Row row: rows){
                        TargetRole role = new TargetRole();
                        role.setId(row.getLong("id"));
                        role.setAppid(row.getLong("app_id"));
                        role.setAction(row.getString("action"));
                        role.setObject(row.getString("object"));
                        return client.preparedQuery("SELECT role_id FROM auth_rbac.target_role WHERE target_id = $1")
                                .execute(Tuple.of(role.getId()))
                                .flatMap(rs -> {
                                    List<Long> roleIds = new ArrayList<>();
                                    for (Row r: rs){
                                        roleIds.add(r.getLong("role_id"));
                                    }
                                    return getRoles(roleIds).map(rs1 -> {
                                        role.setRoles(rs1);
                                        return role;
                                    });
                                });
                    }
                    return Future.succeededFuture(null);
                });
        return Future.all(roles,target).map(v -> new Action<>(roles.result(), target.result()));
    }

    private Future<List<Role>> getRoles(List<Long> ids) {
        if (ids.isEmpty()) return Future.succeededFuture(Collections.emptyList());
        return client.preparedQuery("SELECT id,description,name FROM auth_rbac.role WHERE id IN " + StringUtils.union(ids))
                .execute(Tuple.from(ids))
                .map(rows -> {
                    List<Role> roles = new ArrayList<>();
                    for (Row row: rows){
                        Role role = new Role();
                        role.setId(row.getLong("id"));
                        role.setDescription(row.getString("description"));
                        role.setName(row.getString("name"));
                        roles.add(role);
                    }
                    return roles;
                });
    }


    private void enforce0(Action<List<UserRole>, TargetRole> info, Action<RbacUserInfo, TargetInfo> source) throws AuthException {
        if (info.target() == null) throw new AuthException("No matching [role-config] found for " + source.target());
        List<Role> permission = info.owned().stream().map(UserRole::getRole).toList();
        List<Role> target = info.target().getRoles();
        if (permission.isEmpty())
            throw new AuthException("No matching role found for " + source.owned() + ", with " + source.target());
        for (Role role : target) {
            for (Role permissionRole : permission) {
                if (role.getId().equals(permissionRole.getId())) return;
            }
        }
        throw new AuthException("No match role exists for Owner " + source.owned() + " owned permission " + permission +
                ", with " + source.target() + " which required " + target);
    }

}