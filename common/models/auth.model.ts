export interface LoginRequest {
  username?: string;
  usernameOrEmail?: string;
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

export interface VerifyOldPasswordRequest {
  usernameOrEmail: string;
  oldPassword: string;
  newPassword: string;
}

export interface ForgotPasswordRequestDto {
  usernameOrEmail: string;
}

export interface ResetPasswordWithTokenDto {
  token: string;
  usernameOrEmail: string;
  newPassword: string;
}

export interface ChangePasswordRequest {
  oldPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export interface PasswordActionResponse {
  success: boolean;
  message: string;
  token?: string;
  resetUrl?: string;
  email?: string;
  maskedEmail?: string;
  emailSent?: boolean;
}
