import HttpService from "@/helper/HttpService"
import type { GetServerSidePropsContext } from "next"

class MarginAnalysisApi extends HttpService<any> {
  getListMarginAnalysis = (data: any) => {
    return this.post<any>(`marginAnalystData`, {
      ...data,
    })
  }

  getListMarginAnalysisSummary = (data: any) => {
    return this.post<any>(`marginAnalystSummary`, {
      ...data,
    })
  }

  getDealerList = () => {
    return this.get<any>(`marginAnalystData/getDealers`)
  }
}

const marginAnalysisApi = new MarginAnalysisApi("bookingOrder")

export default marginAnalysisApi
