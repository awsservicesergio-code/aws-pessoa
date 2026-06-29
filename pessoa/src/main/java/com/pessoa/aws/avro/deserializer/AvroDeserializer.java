package com.pessoa.aws.avro.deserializer;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.springframework.stereotype.Service;

@Service
public class AvroDeserializer {

    /**
     * Método responsável por desserializar a mensagem obtida no payload.
     * @param payload
     * @param schema
     * @return GenericRecord
     */
    public GenericRecord deserialize(byte[] payload, Schema schema) {
        try {
            SpecificDatumReader<GenericRecord> reader = new SpecificDatumReader<>(schema);
            BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(payload, null);
            return reader.read(null, decoder);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}