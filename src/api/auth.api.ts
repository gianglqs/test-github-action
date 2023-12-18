import HttpService from '@/helper/HttpService';

class AuthApi extends HttpService {
   resetPassword = (data: any) => {
      return this.post<any>(`/users/resetPassword`, { ...data });
   };

   logOut = () => {
      return this.post<any>('/oauth/revokeAccessToken', {});
   };

   checkToken = () => {
      return this.post<any>('/users/checkToken');
   };
}

const authApi = new AuthApi('');

export default authApi;
