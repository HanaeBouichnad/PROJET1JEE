import { Routes } from '@angular/router';
import { Accounts } from './accounts/accounts';
import { Customers } from './customers/customers';
import {BotUi} from './bot-ui/bot-ui';

export const routes: Routes = [
  { path: "accounts", component: Accounts },
  { path: "customers", component: Customers },
  { path: "bot", component: BotUi }
];
