import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {SubscriptionConfirmationComponent} from './component/subscription-confirmation/subscription-confirmation.component';
import {SubscriptionComponent} from './component/subscription/subscription.component';
import {InsurableFlightListComponent} from './component/insurable-flight-list/insurable-flight-list.component';
import {FinancialMovementSummaryComponent} from "./component/financial-movement-summary/financial-movement-summary.component";

const routes: Routes = [
  {path: '', redirectTo: 'subscription', pathMatch: 'full'},
  {path: 'subscription', component: SubscriptionComponent},
  {path: 'finance/movements', component: FinancialMovementSummaryComponent},
  {path: 'flights/insurable', component: InsurableFlightListComponent},
  {path: 'billing/succeeded', component: SubscriptionConfirmationComponent},
  {path: 'billing/cancelled', component: SubscriptionConfirmationComponent},
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [
    RouterModule
  ]
})
export class AppRoutingModule {
}
