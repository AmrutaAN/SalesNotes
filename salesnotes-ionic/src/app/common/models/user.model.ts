export type UserRole = 'SYSADMIN' | 'companyadmin' | 'companyemployee';

export interface User {
  id?: number;
  username: string;
  fullname: string;
  email: string;
  password?: string;
  role: UserRole;
  active: boolean;
  createdat?: string;
  updatedat?: string;
}

export interface UserRequest {
  username: string;
  fullname: string;
  email: string;
  password?: string;
  role: UserRole;
  active?: boolean;
}
