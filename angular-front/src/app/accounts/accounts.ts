import { Component, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AsyncPipe, DatePipe, DecimalPipe } from '@angular/common';
import { catchError, map, Observable, of } from 'rxjs';
import { Account, AccountsListState, RequestStatus } from '../model/account.model';
import { LoadingService } from '../services/loading';

@Component({
  selector: 'app-accounts',
  imports: [AsyncPipe, DecimalPipe, DatePipe],
  templateUrl: './accounts.html',
  styleUrl: './accounts.css',
})
export class Accounts {
  private http = inject(HttpClient);
  public loadingService = inject(LoadingService);
  protected readonly RequestStatus = RequestStatus;

  accounts$: Observable<AccountsListState> = this.http.get<Account[]>
  ("http://localhost:8888/BANK-ACCOUNT-SERVICE/bankAccounts")
    .pipe(
      map(resp => {
        return { accounts: resp, status: RequestStatus.SUCCESS }
      }),
      catchError((err, caught) => {
        return of({ status: RequestStatus.ERROR, errorMessage: err.statusText })
      })
    )
}
