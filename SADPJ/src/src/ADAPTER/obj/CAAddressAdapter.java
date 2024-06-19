package src.ADAPTER.obj;


class CAAddressAdapter extends AddressValidator {

  private CAAddress objCAAddress;

  public CAAddressAdapter(CAAddress address) {
    objCAAddress = address;
  }
  public boolean isValidAddress(String inp_address,
      String inp_zip, String inp_state) {

    return objCAAddress.isValidCanadianAddr(inp_address,
           inp_zip, inp_state);
  }
}// end of class


