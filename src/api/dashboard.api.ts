import HttpService from "@/helper/HttpService"
import type { GetServerSidePropsContext } from "next"

class DashboardApi extends HttpService<any> {
  getUser = (
    params = {} as Record<string, any>,
    context: GetServerSidePropsContext = null
  ) => {
    return this.get<any>("users", params, context)
  }

  createUser = (data: any) => {
    return this.post<any>(`users`, { ...data })
  }

  getDetailUser = (
    params = {} as Record<string, any>,
    context: GetServerSidePropsContext = null
  ) => {
    return this.get<any>("users/getDetails/24", params, context)
  }

  deactivateUser = (userId: any) => {
    return this.put<any>(`users/activate/${userId}`, { userId })
  }
}

const dashboardApi = new DashboardApi("dashboard")

export default dashboardApi
