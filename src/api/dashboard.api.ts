import HttpService from "@/helper/HttpService";
import type { GetServerSidePropsContext } from "next";

class DashboardApi extends HttpService<any> {
  getUser = (
    params = {} as Record<string, any>,
    context: GetServerSidePropsContext = null
  ) => {
    return this.get<any>("users", params, context);
  };
}

const dashboardApi = new DashboardApi("dashboard");

export default dashboardApi;
