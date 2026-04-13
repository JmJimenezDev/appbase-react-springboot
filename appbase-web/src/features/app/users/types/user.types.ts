export interface UserDTO {
  id: number;
  name: string;
  surnames: string;
  email: string;
  phone: string;
  enabled: boolean;
  emailVerified: boolean;
  roles: string[];
  createdAt?: Date;
}

export interface UpdateUserParams {
  id: number;
  data: Partial<UserDTO>;
}
