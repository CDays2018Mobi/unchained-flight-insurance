import {Component, OnInit} from '@angular/core';
import {SelectItem} from 'primeng/api';
import {ContractClient} from '../../service/contract-client.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Contract} from '../../model/contract.model';
import * as moment from 'moment';

@Component({
  selector: 'app-subscription',
  templateUrl: './subscription.component.html',
  styleUrls: ['./subscription.component.css']
})
export class SubscriptionComponent implements OnInit {

  levels: SelectItem[];
  selectedLevel: string;
  form: FormGroup;

  constructor(private fb: FormBuilder,
              private contracClient: ContractClient) {
    this.levels = [
      {label: 'Basic', value: 'Basic'},
      {label: 'Medium', value: 'Medium'},
      {label: 'Ultimate', value: 'Ultimate'}
    ];
  }

  ngOnInit() {
    this.form = this.fb.group({
        flightId: ['', [Validators.required]],
        arrivalDate: ['', [Validators.required]],
        ticketId: ['', [Validators.required]],
        email: ['', [Validators.required]],
      }
    );
  }

  submit() {
    this.contracClient.create$(
      new Contract(
        this.form.value.flightId,
        moment(this.form.value.arrivalDate).format('YYYY-MM-DD')))
        .subscribe(contract => console.log(JSON.stringify(contract)));
  }
}
