import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import {ButtonModule} from 'primeng/button';
import {CalendarModule, DropdownModule} from 'primeng/primeng';
import {ListboxModule} from 'primeng/listbox';

import { AppComponent } from './app.component';
import { SubscriptionComponent } from './component/subscription/subscription.component';
import { BillingComponent } from './component/billing/billing.component';
import {ContractClient} from './service/contract-client.service';
import {ReactiveFormsModule} from "@angular/forms";
import {HttpClientModule} from "@angular/common/http";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";


@NgModule({
  declarations: [
    AppComponent,
    SubscriptionComponent,
    BillingComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    ButtonModule,
    DropdownModule,
    CalendarModule,
    ListboxModule,
    HttpClientModule,
  ],
  providers: [ContractClient],
  bootstrap: [AppComponent]
})
export class AppModule { }
