import HttpService from '@/helper/HttpService';
import type { GetServerSidePropsContext } from 'next';
import { ResponseType } from 'axios';

class IndicatorApi extends HttpService<any> {
   getInitDataFilter = () => {
      return this.post<any>(`filters/competitorPricing`);
   };

   getDataLineChartPlant = <T = any>(
      data = {} as Record<string, any>,
      params = {} as Record<string, any>,
      context: GetServerSidePropsContext = null as any,
      responseType = 'default' as ResponseType
   ) => {
      this.saveToken(context);
      return this.instance.post<T>(`charts/lineChartRegion`, data, { params, responseType });
   };

   getDataBubbleChart = (data: any) => {
      return this.post<any>(`bubbleChart`, { ...data });
   };

   getIndicator = (data: any, pageNo: number, perPage: number) => {
      return this.post<any>(`indicator?pageNo=${pageNo}&perPage=${perPage}`, {
         ...data,
      });
   };

   getDataLineChartRegion = <T = any>(
      data = {} as Record<string, any>,
      params = {} as Record<string, any>,
      context: GetServerSidePropsContext = null as any,
      responseType = 'default' as ResponseType
   ) => {
      this.saveToken(context);
      return this.instance.post<T>(`charts/lineChartRegion`, data, { params, responseType });
   };
}

const indicatorApi = new IndicatorApi('indicator');

export default indicatorApi;
