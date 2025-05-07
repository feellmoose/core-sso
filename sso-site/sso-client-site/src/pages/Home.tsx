import { Outlet } from "react-router";
import { Background } from "@/components/base/Background";
const Home = () => {
  return (
    <Background>
      <main className=" w-screen h-screen">
        <Outlet />
      </main>
    </Background>
  );
};
export default Home;