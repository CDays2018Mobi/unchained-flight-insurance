import { Component } from '@angular/core';
import {ContractService} from "./service/contract.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  constructor(private contractService: ContractService) {
  }
}
