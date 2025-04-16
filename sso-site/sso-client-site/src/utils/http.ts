import axios, { type AxiosRequestConfig, type AxiosResponse } from "axios";
import type { Response } from "./response";
import { showSnack } from "@/utils/showSnack";
export function Http<T = any>(
  url: string,
  header: Record<string, string> = {},
  hooks: {
    redirectToLogin?: () => void;
    errorHandler?: (response: AxiosResponse) => void;
    beforeRequest?: (config: AxiosRequestConfig) => AxiosRequestConfig;
  } = {}
) {
  let config: AxiosRequestConfig = {};
  config.headers = header;
  config.baseURL = import.meta.env.VITE_API_BASE_URL;

  const { redirectToLogin, errorHandler, beforeRequest } = hooks;
//   const navigate = useNavigate();

  config.headers.Authorization = localStorage.getItem("token") || "";
  if (beforeRequest) {
    config = beforeRequest(config);
  }
  //axios包装的那一层的http状态
  const STATUS = {
    SUCCESS: 200,
    UNAUTHORIZED: 401,
  };
  //自己和服务端协商的code格式
  const CODE = {
    SUCCESS: 200,
    UNAUTHORIZED: 401,
  };
  let response: Promise<AxiosResponse<Response<T>>>;

  async function respond<T = any>(
    response: Promise<AxiosResponse<Response<T>>>
  ) {
    return new Promise<T>(async (resolve, reject) => {
      const res = await response;
      if (res.status !== STATUS.SUCCESS) {
        if (errorHandler) {
          errorHandler(res);
        } else {
          showSnack("请求出现异常，请稍后再试", { variant: "error" });
        }
        reject(res);
        return;
      }
      if (res.data.code === CODE.UNAUTHORIZED) {
        if (redirectToLogin) {
          redirectToLogin();
        } else {
          showSnack("登录信息已过期，请重新登录", { variant: "error" });
          //这里改成跳到login
        //   navigate({ to: ".." });
        }
        reject("登录信息已过期，请重新登录");
        return;
      }
      if (res.data.code !== CODE.SUCCESS) {
        if (errorHandler) {
          errorHandler(res);
        } else {
          showSnack(res.data.message, { variant: "error" });
        }
        reject(res.data);
        return;
      }
      resolve(res.data.data);
    });
  }

  async function post(data?: any): Promise<T> {
    response = axios.post(url, data, config);
    return respond<T>(response);
  }
  async function get(): Promise<T> {
    response = axios.get(url, config);
    return respond<T>(response);
  }
  return {
    post,
    get,
  };
}
