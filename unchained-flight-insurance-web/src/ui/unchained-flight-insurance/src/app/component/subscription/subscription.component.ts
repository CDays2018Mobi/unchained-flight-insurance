import {Component, OnInit} from '@angular/core';
import {SelectItem} from 'primeng/api';
import {ContractClient} from '../../service/contract-client.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Contract} from '../../model/contract.model';
import {RiskClient} from '../../service/risk-client.service';
import {RiskCoverage} from '../../model/coverage.model';
import {Flight} from '../../model/flight.model';
import * as moment from 'moment';

@Component({
  selector: 'app-subscription',
  templateUrl: './subscription.component.html',
  styleUrls: ['./subscription.component.css']
})
export class SubscriptionComponent implements OnInit {

  riskCoverageItems: SelectItem[] = [];
  form: FormGroup;

  constructor(private fb: FormBuilder,
              private contracClient: ContractClient,
              private riskClient: RiskClient) {
  }

  ngOnInit() {
    this.form = this.fb.group({
        flightId: ['', [Validators.required]],
        arrivalDate: ['', [Validators.required]],
        ticketId: [''],
        riskCoverage: ['', [Validators.required]],
        email: ['', [Validators.required]],
      }
    );
  }

  currentFlight(): Flight {
    return new Flight(
      this.form.value.flightId,
      moment(this.form.value.arrivalDate).format('YYYY-MM-DD'));
  }

  currentRiskCoverage(): RiskCoverage {
    return this.form.value.riskCoverage;
  }

  flightChanged() {
    const flight = this.currentFlight();

    if (flight.flightId && flight.expectedArrivalDate) {
      this.riskClient.getRiskCoverages$(flight.flightId, flight.expectedArrivalDate).subscribe(coverages => {
        this.riskCoverageItems = coverages.map(coverage => {
          return <SelectItem>{label: coverage.name, value: coverage}
        });
      });
    } else {
      this.riskCoverageItems = [];
    }
  }

  submit() {
    const flight: Flight = this.currentFlight();
    const coverage: RiskCoverage = this.currentRiskCoverage();
    const contract = new Contract(flight.flightId, flight.expectedArrivalDate);

    window.location.href =
      'https://pilot.datatrans.biz/upp/jsp/upStart.jsp' +
      '?merchantId=1100004624' +
      '&refno=1337' +
      `&amount=${coverage.premiumAmount * 100}` +
      '&currency=CHF' +
      '&paymentmethod=ECA' +
      '&paymentmethod=VIS' +
      '&theme=DT2015' +
      '&successUrl=http://localhost:9000/api/v1/billing/payed?flightId=' + flight.flightId +
      '&cancelUrl=http://localhost:9000/api/v1/billing/cancelled?flightId=' + flight.flightId;
  }
}
