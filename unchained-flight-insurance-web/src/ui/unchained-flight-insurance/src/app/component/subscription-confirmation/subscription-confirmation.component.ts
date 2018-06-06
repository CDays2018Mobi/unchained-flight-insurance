import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {ContractClient} from '../../service/contract-client.service';

@Component({
  selector: 'app-subscription-confirmation',
  templateUrl: './subscription-confirmation.component.html',
  styleUrls: ['./subscription-confirmation.component.css']
})
export class SubscriptionConfirmationComponent implements OnInit {

  fulfilled = false;

  constructor(private router: Router, private contractClient: ContractClient) {
  }

  ngOnInit() {
    setTimeout(() => this.fulfilled = true, 1000);
  }

  backToHome() {
    this.router.navigate(['subscription']);
  }
}
