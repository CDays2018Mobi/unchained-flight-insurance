import {RiskCoverage} from './coverage.model';

type FlightStatus = 'On time' | 'Delayed' | 'Canceled';

export class Flight {
  constructor(public flightId: string,
              public expectedArrivalDate: string,
              public status ?: FlightStatus) {
  }
}

export class InsurableFlight {
  constructor(public flight: Flight,
              public delayProbability: number,
              public riskCoverages: RiskCoverage[]) {
  }
}

export class Ticket {
  constructor(public flightId: string,
              public arrivalDate: string,
              public ticketId: string) {
  }
}
