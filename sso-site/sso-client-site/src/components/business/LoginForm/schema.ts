import { z } from "zod";
export const LoginFormSchema = z.object({
  username: z.string().min(1, "用户名不能为空"),
  password: z.string().min(8, "密码长度至少8位"),
});

export type LoginFormType = z.infer<typeof LoginFormSchema>