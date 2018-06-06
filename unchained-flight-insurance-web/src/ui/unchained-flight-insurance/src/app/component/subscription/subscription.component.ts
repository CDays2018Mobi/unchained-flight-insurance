import {Component, OnInit} from '@angular/core';
import {SelectItem} from 'primeng/api';
import {ContractClient} from '../../service/contract-client.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Contract} from '../../model/contract.model';
import * as moment from 'moment';
import {RiskClient} from "../../service/risk-client.service";
import {Coverage} from "../../model/coverage.model";

@Component({
  selector: 'app-subscription',
  templateUrl: './subscription.component.html',
  styleUrls: ['./subscription.component.css']
})
export class SubscriptionComponent implements OnInit {

  coverageLevels: SelectItem[] = [];
  form: FormGroup;

  constructor(private fb: FormBuilder,
              private contracClient: ContractClient,
              private riskClient: RiskClient) {
  }

  ngOnInit() {
    this.riskClient.getCoverages$().subscribe(coverages => {
      this.coverageLevels = coverages.map(coverage => {
        return <SelectItem>{label: coverage.name, value: coverage}
      });
    });

    this.form = this.fb.group({
        flightId: ['', [Validators.required]],
        arrivalDate: ['', [Validators.required]],
        ticketId: [''],
        coverageLevel: ['', [Validators.required]],
        email: ['', [Validators.required]],
      }
    );
  }

  submit() {
    const contract = new Contract(
      this.form.value.flightId,
      moment(this.form.value.arrivalDate).format('YYYY-MM-DD'));

    const coverage: Coverage = this.form.value.coverageLevel;

    window.location.href =
      "https://pilot.datatrans.biz/upp/jsp/upStart.jsp" +
      "?merchantId=1100004624" +
      "&refno=1337" +
      `&amount=${coverage.premiumAmount * 100}` +
      "&currency=CHF" +
      "&paymentmethod=ECA" +
      "&paymentmethod=VIS" +
      "&theme=DT2015" +
      "&successUrl=http://localhost:9000/api/v1/billing/payed" +
      "&cancelUrl=http://localhost:9000/api/v1/billing/cancelled";
  }
}
