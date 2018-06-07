export class RiskCoverage {
  constructor(public name: string,
              public insuredAmount: number,
              public premiumAmount: number,
              public available: boolean,
              public url?: string) {
  }
}
