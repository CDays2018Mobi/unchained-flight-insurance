import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FinancialMovementListComponent } from './financial-movement-list.component';

describe('FinancialMovementListComponent', () => {
  let component: FinancialMovementListComponent;
  let fixture: ComponentFixture<FinancialMovementListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FinancialMovementListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FinancialMovementListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
