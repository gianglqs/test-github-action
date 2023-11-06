import HttpService from '@/helper/HttpService';
import type { GetServerSidePropsContext } from 'next';

class IndicatorApi extends HttpService<any> {
  getInitDataFilter = () => {
    return this.get<any>(`filters`);
  };

  getIndicator = (data: any, pageNo: number, perPage: number) => {
    return this.post<any>(`bookingOrders?pageNo=${pageNo}&perPage=${perPage}`, {
      ...data,
    });
  };
}

const indicatorApi = new IndicatorApi('bookingOrder');

export default indicatorApi;
