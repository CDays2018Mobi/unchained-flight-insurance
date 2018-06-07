import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InsurableFlightListComponent } from './insurable-flight-list.component';

describe('InsurableFlightListComponent', () => {
  let component: InsurableFlightListComponent;
  let fixture: ComponentFixture<InsurableFlightListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InsurableFlightListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InsurableFlightListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
