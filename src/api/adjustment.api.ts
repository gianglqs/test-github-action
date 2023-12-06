import HttpService from '@/helper/HttpService';
import type { GetServerSidePropsContext } from 'next';
import { ResponseType } from 'axios';

class AdjustmentApi extends HttpService<any> {
   getInitDataFilter = () => {
      return this.get<any>(`filters/booking`);
   };

   getAdjustment = <T = any>(
      data = {} as Record<string, any>,
      params = {} as Record<string, any>,
      context: GetServerSidePropsContext = null as any,
      responseType = 'default' as ResponseType
   ) => {
      this.saveToken(context);
      return this.instance.post<T>(`getAdjustmentData`, data, { params, responseType });
   };
}

const adjustmentApi = new AdjustmentApi('adjustment');

export default adjustmentApi;
