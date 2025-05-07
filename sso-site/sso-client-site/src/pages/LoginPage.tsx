import { Reveal } from "@/components/base/Reveal";
import { LoginForm } from "@/components/business/LoginForm";
import Logo from "@/assets/logo.svg";
const LoginPage = () => {
  return (
    <Reveal className=" relative h-screen w-screen flex justify-center items-center">
      <div className=" absolute top-5 left-5 flex items-center gap-2">
        <img className=" block w-36 h-36" src={Logo} alt="feellmoose" />
        <h2 className=" text-3xl font-bold">𝓯𝓮𝓮𝓵𝓵𝓶𝓸𝓸𝓼𝓮</h2>
      </div>
      <LoginForm className=" w-80 h-60" />
    </Reveal>
  );
};

export default LoginPage;
