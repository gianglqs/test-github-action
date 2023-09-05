import axios, { AxiosInstance, ResponseType } from 'axios'
import nookies, { parseCookies } from 'nookies'
import { plural } from 'pluralize'
import type { GetServerSidePropsContext } from 'next'
import Router from 'next/router'

class HttpService {
    private instance: AxiosInstance

    protected entity: string

    constructor(entity: string) {
        this.entity = plural(entity)
        axios.defaults.withCredentials = true
        this.instance = axios.create({
        baseURL: "http://192.168.1.154:8080/",
        })
        this.instance.interceptors.response.use(this.handleSuccessRes, this.handleErrorRes)
    }

    private handleSuccessRes({ data, status }) {
        return { data, status } as any
    }

    private handleErrorRes(error) {
        let formatError = {}
        if (error?.response) {
          const { data, status } = error.response
          const isServer = typeof window === 'undefined'
          switch (status) {
            case 401:
              nookies.destroy(null, 'token')
              if (!isServer) Router.replace('/login')
              break
            case 503:
              if (!isServer) Router.replace('/maintenance')
              break
            default:
              break
          }
          const { message, ...restData } = data
          formatError = { message, status, ...restData }
        }
        // Aborted request case
        if (error?.code === 'ECONNABORTED') {
          formatError = { message: 'Request aborted', status: 'canceled' }
        }
        // For dev only
        if (process.env.NEXT_PUBLIC_MODE === 'develop') {
          console.log('error', '>>>', error)
          console.log('error to JSON', '>>>', error.toJSON())
        }
        return Promise.reject(formatError)
    }
    
    private saveToken = (context: GetServerSidePropsContext = null as any) => {
      let cookies = {} as Record<string, string>
      if (context) {
        cookies = nookies.get(context)
      } else {
        cookies = parseCookies()
      }
  
      const token = cookies['token']
      
      // Set token if had
      if (token) {
        this.instance.defaults.headers.common.Authorization = `Bearer${token}`
      }
      else {
        delete this.instance.defaults.headers.Authorization
      }
    }

    get = <T = any>(
      endpoint: string,
      params = {} as Record<string, any>,
      context: GetServerSidePropsContext = null,
      responseType = 'default' as ResponseType
    ) => {
      this.saveToken(context)
      return this.instance.get<T>(endpoint, { params, responseType: responseType })
    }

    getList = (params = {} as Record<string, any>, context: GetServerSidePropsContext = null) =>
      this.get<any>(this.entity, params, context)

    getListUser = <T = any>(
      endpoint: string,
      context: GetServerSidePropsContext = null,
    ) => {
      this.saveToken(context)
      return this.instance.get<T>(endpoint)
    }

    post = <T = any>(endpoint: string, data = {} as Record<string, any>,context: GetServerSidePropsContext = null as any) => {
      this.saveToken(context)
      return this.instance.post<T>(endpoint, data)
    }
}

export default HttpService