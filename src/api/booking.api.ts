import HttpService from "@/helper/HttpService"
import type { GetServerSidePropsContext } from "next"

class BookingApi extends HttpService<any> {
  getInitDataFilter = () => {
    return this.get<any>(`filters`)
  }

  getBooking = (data: any, pageNo: number, perPage: number) => {
    return this.post<any>(`bookingOrders?pageNo=${pageNo}&perPage=${perPage}`, {
      ...data,
    })
  }
}

const bookingApi = new BookingApi("bookingOrder")

export default bookingApi
