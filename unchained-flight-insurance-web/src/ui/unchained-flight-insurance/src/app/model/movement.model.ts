export class Movement {
  constructor(public date: string,
              public beneficiary: string,
              public amount: number) {
  }
}

export class MovementSummary {
  constructor(public effectiveBalance: number,
              public predictedBalance: number,
              public effective: Movement[],
              public predicted: Movement[]) {}
}
