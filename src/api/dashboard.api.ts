import HttpService from "@/helper/HttpService";
import type { GetServerSidePropsContext } from "next";

class DashboardApi extends HttpService<any> {
  getUser = (
    params = {} as Record<string, any>,
    context: GetServerSidePropsContext = null
  ) => {
    return this.get<any>("users", params, context);
  };

  createUser = (data: any) => {
    return this.post<any>(`users`, { ...data })
  }
}

const dashboardApi = new DashboardApi("dashboard");

export default dashboardApi;
