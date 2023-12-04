import HttpService from '@/helper/HttpService';

class AuthApi extends HttpService {
   login = (params: Omit<any, 'remember'>) => this.post<any>('oauth/token', params);

   resetPassword = (data: any) => {
      return this.post<any>(`/users/resetPassword`, { ...data });
   };

   checkToken = () => {
      return this.post<any>('/users/checkToken');
   };
}

const authApi = new AuthApi('');

export default authApi;
