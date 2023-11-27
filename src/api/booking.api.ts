import HttpService from '@/helper/HttpService';
import type { GetServerSidePropsContext } from 'next';

class BookingApi extends HttpService<any> {
   getInitDataFilter = () => {
      return this.get<any>(`filters/booking`);
   };
}

const bookingApi = new BookingApi('bookingOrder');

export default bookingApi;
