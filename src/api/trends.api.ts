import HttpService from '@/helper/HttpService';
import { GetServerSidePropsContext } from 'next';
import { ResponseType } from 'axios';

class TrendsApi extends HttpService<any> {
   getInitDataFilter = () => {
      return this.get<any>(`filters/trends`);
   };

   getMarginVsCostData = <T = any>(
      data = {} as Record<string, any>,
      params = {} as Record<string, any>,
      context: GetServerSidePropsContext = null as any,
      responseType = 'default' as ResponseType
   ) => {
      this.saveToken(context);
      return this.instance.post<T>(`trends/getMarginVsCostData`, data, { params, responseType });
   };

   getMarginVsDNData = <T = any>(
      data = {} as Record<string, any>,
      params = {} as Record<string, any>,
      context: GetServerSidePropsContext = null as any,
      responseType = 'default' as ResponseType
   ) => {
      this.saveToken(context);
      return this.instance.post<T>(`trends/getMarginVsDNData`, data, { params, responseType });
   };
}

const trendsApi = new TrendsApi('trend');

export default trendsApi;
