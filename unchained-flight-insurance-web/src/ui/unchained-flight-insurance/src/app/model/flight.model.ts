export class Flight {
  constructor(public flightId: string,
              public arrivalDate: string) {
  }
}

export class Ticket {
  constructor(public flightId: string,
              public arrivalDate: string,
              public ticketId: string) {
  }
}
