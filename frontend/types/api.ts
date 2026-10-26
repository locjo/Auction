export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface ApiErrorBody {
  errorCode?: string;
  status?: number;
  message?: string;
  path?: string;
}
