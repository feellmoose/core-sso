import { FormContainer } from "@components/Form/FormContainer";
import { InputController } from "@components/Form/controllers/InputController";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import { LoginFormSchema, LoginFormType } from "./schema";

interface LoginFormProps {
  className?: string;
}
export const LoginForm: React.FC<LoginFormProps> = ({ className }) => {
  return (
    <FormContainer
      className={cn(
        className,
        " bg-gray-200/40 backdrop-blur-2xl flex items-center justify-center p-4 border shadow-2xl rounded-2xl"
      )}
      defaultValues={{
        username: "",
        password: "",
      }}
      onSubmit={(data: LoginFormType) => {
        console.log(data);
      }}
      onError={(errors) => {
        console.log(errors);
      }}
      schema={LoginFormSchema}
    >
      <div className=" w-full flex flex-col gap-4">
        <InputController<LoginFormType>
          placeholder="请输入用户名"
          name="username"
          type="text"
        />
        <InputController<LoginFormType>
          placeholder="密码"
          name="password"
          type="password"
        />
        <Button className=" cursor-pointer" type="submit">SSO 登录</Button>
      </div>
    </FormContainer>
  );
};
