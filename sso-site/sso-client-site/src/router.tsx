import { createBrowserRouter, createRoutesFromElements, Route } from 'react-router-dom';
import  Home  from './pages/Home';
import LoginPage from './pages/LoginPage';

export const router = createBrowserRouter(
  createRoutesFromElements(
    <Route path="/" element={<Home />}>
      <Route index element={<LoginPage />} /> // 默认子路由
    </Route>
  )
);