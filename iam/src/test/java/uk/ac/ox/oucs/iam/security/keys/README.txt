In order to run these tests the following seed files are required

For CryptServicesTest
cryptkey.source - symmetric key generated on a remote machine
encString0-5 - encrypted messages from the remote machine that we need to decrypt
permanentSymKey - a file created during the first run of the tests and then used in subsequent runs

For HMacTest
Inputs: remote_MacKey - Mac key generated on a remote machine
hMacString.source.remote - string encrypted on the remote machine using remote_MacKey
(this has been generated using "This is a secret HMAC message", which is defined as a variable in the test)

All files are currently expected in the project's 'testfiles' subfolder.