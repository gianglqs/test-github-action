import HttpService from '@/helper/HttpService'

class AuthApi extends HttpService {
    login = (params: Omit<any, 'remember'>) => this.post<any>('oauth/token', params)
}

const authApi = new AuthApi('')

export default authApi