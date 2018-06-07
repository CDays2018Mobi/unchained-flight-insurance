import {Component, Input} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {Movement} from '../../model/movement.model';
import 'rxjs/add/operator/map';

@Component({
  selector: 'app-financial-movement-list',
  templateUrl: './financial-movement-list.component.html',
  styleUrls: ['./financial-movement-list.component.css']
})
export class FinancialMovementListComponent {

  @Input() movements$: Observable<Movement[]>;
}
