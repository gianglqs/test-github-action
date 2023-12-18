import { parseCookies } from 'nookies';
import { ReactNode, createContext } from 'react';

export const UserInfoContext = createContext(null);

export default function UserInfoProvider({ children }: { children: ReactNode }) {
   let cookies = parseCookies();
   let userRoleCookies = cookies['role'];
   console.log('Role: ' + userRoleCookies);
   return (
      <UserInfoContext.Provider value={{ userRole: userRoleCookies }}>
         {children}
      </UserInfoContext.Provider>
   );
}
