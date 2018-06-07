import {Component, OnInit} from '@angular/core';
import {Flight, InsurableFlight} from '../../model/flight.model';
import {RiskCoverage} from '../../model/coverage.model';

@Component({
  selector: 'app-insurable-flight-list',
  templateUrl: './insurable-flight-list.component.html',
  styleUrls: ['./insurable-flight-list.component.css']
})
export class InsurableFlightListComponent implements OnInit {

  insurableFlights: InsurableFlight[] = [
    new InsurableFlight(new Flight('GVA123', '2018-06-07'), 0.5, [
      new RiskCoverage('Basic', 250, 50, true),
      new RiskCoverage('Medium', 500, 80, true),
      new RiskCoverage('Ultimate', 10000, 200, false)
    ]),
    new InsurableFlight(new Flight('BRA2424', '2018-06-15'), 0.9, [
      new RiskCoverage('Basic', 250, 700, true),
      new RiskCoverage('Medium', 500, 1200, false),
      new RiskCoverage('Ultimate', 10000, 3000, false)
    ]),
    new InsurableFlight(new Flight('DID69', '2018-07-22'), 0.1, [
      new RiskCoverage('Basic', 100, 10, true),
      new RiskCoverage('Medium', 500, 20, true),
      new RiskCoverage('Ultimate', 10000, 30, true)
    ])
  ];

  constructor() {
  }

  ngOnInit() {
  }

}
