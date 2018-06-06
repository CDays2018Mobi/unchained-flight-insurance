import {ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {ButtonModule} from 'primeng/button';
import {CalendarModule, DropdownModule, InputTextModule, ProgressSpinnerModule} from 'primeng/primeng';
import {ListboxModule} from 'primeng/listbox';
import {MessageModule} from 'primeng/message';
import {CardModule} from 'primeng/card';

import {AppComponent} from './app.component';
import {SubscriptionComponent} from './component/subscription/subscription.component';
import {ContractClient} from './service/contract-client.service';
import {RiskClient} from './service/risk-client.service';
import {SubscriptionConfirmationComponent} from './component/subscription-confirmation/subscription-confirmation.component';
import {NotifierService} from './service/notifier-service';
import {AppRoutingModule} from './app-routing.module';
import {MessageService} from "primeng/components/common/messageservice";
import {MessagesModule} from 'primeng/messages';

@NgModule({
  declarations: [
    AppComponent,
    SubscriptionComponent,
    SubscriptionConfirmationComponent,
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
    InputTextModule,
    CardModule,
    MessageModule,
    MessagesModule,
    ProgressSpinnerModule,
    AppRoutingModule
  ],
  providers: [ContractClient, RiskClient, NotifierService, MessageService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
