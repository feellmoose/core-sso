package com.qingyou.sso.auth.internal.rbac;

import com.qingyou.sso.auth.api.dto.Action;
import com.qingyou.sso.auth.exception.AuthException;
import com.qingyou.sso.domain.auth.Role;
import com.qingyou.sso.domain.auth.TargetRole;
import com.qingyou.sso.domain.auth.UserRole;
import com.qingyou.sso.utils.UniConvertUtils;
import io.vertx.core.Future;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.reactive.mutiny.Mutiny;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class Rbac implements IRbac<RbacUserInfo, TargetInfo> {
    private final Mutiny.SessionFactory sessionFactory;

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
        var roles = sessionFactory.withSession(session ->
                session.createQuery("from UserRole where appid = :appid and userId = :userId", UserRole.class)
                        .setParameter("appid", targetInfo.appId())
                        .setParameter("userId", info.owned().id()).getResultList()
        ).convert().with(UniConvertUtils::toFuture);
        var targets = roles.flatMap(rs ->
                sessionFactory.withSession(session ->
                        session.createQuery("from TargetRole where appid = :appid and action = :action and object = :object", TargetRole.class)
                                .setParameter("appid", targetInfo.appId())
                                .setParameter("action", targetInfo.action())
                                .setParameter("object", targetInfo.object()).getSingleResultOrNull()
                ).convert().with(UniConvertUtils::toFuture)
        );
        return targets.map(v -> new Action<>(roles.result(), targets.result()));
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
