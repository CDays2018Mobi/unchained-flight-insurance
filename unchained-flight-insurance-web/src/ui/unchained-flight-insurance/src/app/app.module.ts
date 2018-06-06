import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';


import { AppComponent } from './app.component';
import { SubscriptionComponent } from './component/subscription/subscription.component';
import { BillingComponent } from './component/billing/billing.component';
import {ButtonModule} from 'primeng/button';
import {CalendarModule, DropdownModule} from 'primeng/primeng';
import {ListboxModule} from 'primeng/listbox';


@NgModule({
  declarations: [
    AppComponent,
    SubscriptionComponent,
    BillingComponent
  ],
  imports: [
    BrowserModule,
    ButtonModule,
    DropdownModule,
    CalendarModule,
    ListboxModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
