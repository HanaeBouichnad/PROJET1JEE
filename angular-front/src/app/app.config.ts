import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {provideMarkdown} from 'ngx-markdown';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {loadingInterceptorInterceptor} from './interceptors/loading-interceptor-interceptor';
import { registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';

registerLocaleData(localeFr);
export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideMarkdown(),
    provideHttpClient(withInterceptors([loadingInterceptorInterceptor]))
  ]
};
