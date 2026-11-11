export interface LoginRequest {
  username: string;
  password: string;
}

export interface AuthUser {
  id: string;
  username: string;
  fullname: string;
  email: string;
  role: string;
  organisationId?: string;
  organisationName?: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken?: string;
  tokenType: string;
  expiresIn?: number;
  user: AuthUser;
}

export interface RoleItem {
  id: string;
  code: string;
  name: string;
  description?: string;
  isSystem: boolean;
  userCount: number;
  createdAt?: string;
  updatedAt?: string;
}
