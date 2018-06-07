import {Component, OnInit} from '@angular/core';
import {InsurableFlight} from '../../model/flight.model';
import {Observable} from 'rxjs/Observable';
import {FlightClient} from '../../service/flight-client.service';

@Component({
  selector: 'app-insurable-flight-list',
  templateUrl: './insurable-flight-list.component.html',
  styleUrls: ['./insurable-flight-list.component.css']
})
export class InsurableFlightListComponent implements OnInit {

  insurableFlights$: Observable<InsurableFlight[]>;

  constructor(private flightClient: FlightClient) {
  }

  ngOnInit() {
    this.insurableFlights$ = this.flightClient.getInsurableFlights$();
  }

}
