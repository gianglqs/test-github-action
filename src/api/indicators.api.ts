import HttpService from '@/helper/HttpService';
import type { GetServerSidePropsContext } from 'next';
import { ResponseType } from 'axios';

class IndicatorApi extends HttpService<any> {
   getInitDataFilter = () => {
      return this.get<any>(`filters/competitorPricing`);
   };

   getDataLineChartPlant = <T = any>(
      data = {} as Record<string, any>,
      params = {} as Record<string, any>,
      context: GetServerSidePropsContext = null as any,
      responseType = 'default' as ResponseType
   ) => {
      this.saveToken(context);
      return this.instance.post<T>(`charts/lineChartPlant`, data, { params, responseType });
   };

   getCompetitiveLandscape = (data: any) => {
      return this.post<any>(`charts/competitiveLandscape`, { ...data });
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

   getIndicators = <T = any>(
      data = {} as Record<string, any>,
      params = {} as Record<string, any>,
      context: GetServerSidePropsContext = null as any,
      responseType = 'default' as ResponseType
   ) => {
      this.saveToken(context);
      return this.instance.post<T>(
         `table/indicator?pageNo=${params.pageNo}&perPage=${params.perPage}`,
         data,
         { params, responseType }
      );
   };
}

const indicatorApi = new IndicatorApi('indicator');

export default indicatorApi;
