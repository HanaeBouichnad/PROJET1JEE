import { Component, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AsyncPipe } from '@angular/common';
import { catchError, map, Observable, of } from 'rxjs';
import { Customer, CustomersListState } from '../model/customer.model';
import { RequestStatus } from '../model/account.model';

@Component({
  selector: 'app-customers',
  imports: [AsyncPipe],
  templateUrl: './customers.html',
  styleUrl: './customers.css',
})
export class Customers {
  private http = inject(HttpClient);
  protected readonly RequestStatus = RequestStatus;

  customers$: Observable<CustomersListState> = this.http.get<Customer[]>
  ("http://localhost:8888/CUSTOMER-SERVICE/customers")
    .pipe(
      map(resp => {
        return { customers: resp, status: RequestStatus.SUCCESS }
      }),
      catchError((err, caught) => {
        return of({ status: RequestStatus.ERROR, errorMessage: err.statusText })
      })
    )
}
