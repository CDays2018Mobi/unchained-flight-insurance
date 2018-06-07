import {Component, OnInit} from '@angular/core';

class InsurableFlight {
  constructor(public flightId: string,
              public status: string,
              public delayProbability: number,
              public premiumAmount: number[]) {
  }
}

@Component({
  selector: 'app-insurable-flight-list',
  templateUrl: './insurable-flight-list.component.html',
  styleUrls: ['./insurable-flight-list.component.css']
})
export class InsurableFlightListComponent implements OnInit {

  insurableFlights: InsurableFlight[] = [
    new InsurableFlight("GVA123", "On time", 0.5, [100, 500, 10000]),
    new InsurableFlight("BRA2424", "On time", 0.2, [100, 500, 10000]),
    new InsurableFlight("DID69", "Delayed", 1, [100, 500, 10000]),
  ];

  constructor() {
  }

  ngOnInit() {
  }

}
