import { Component, OnInit } from '@angular/core';
import {SelectItem} from 'primeng/api';

@Component({
  selector: 'app-subscription',
  templateUrl: './subscription.component.html',
  styleUrls: ['./subscription.component.css']
})
export class SubscriptionComponent implements OnInit {

  levels : SelectItem[];
  selectedLevel: string;

  constructor() {
    this.levels = [
      {label:'Basic', value:'Basic'},
      {label:'Classical', value:'Classical'},
      {label:'Comfort', value:'Comfort'}
    ];
  }

  ngOnInit() {
  }

}
