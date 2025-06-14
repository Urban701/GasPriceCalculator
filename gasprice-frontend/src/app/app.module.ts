import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms'; // Import FormsModule

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FuelPricesComponent } from './components/fuel-prices/fuel-prices.component';
import { TripCalculatorComponent } from './components/trip-calculator/trip-calculator.component';

@NgModule({
  declarations: [
    AppComponent,
    FuelPricesComponent,
    TripCalculatorComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule // Add FormsModule here
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
