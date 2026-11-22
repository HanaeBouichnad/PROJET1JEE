import { RequestStatus } from './account.model';

export interface Customer {
  id?: number,
  name?: string,
  email?: string
}

export interface CustomersListState {
  customers?: Customer[],
  status?: RequestStatus,
  errorMessage?: string
}
