import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TripCalculatorComponent } from './trip-calculator.component';

describe('TripCalculatorComponent', () => {
  let component: TripCalculatorComponent;
  let fixture: ComponentFixture<TripCalculatorComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TripCalculatorComponent]
    });
    fixture = TestBed.createComponent(TripCalculatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
